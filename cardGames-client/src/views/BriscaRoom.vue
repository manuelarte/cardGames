<template>
  <div class="briscaRoom">

    <md-progress-bar v-if="isLoading" md-mode="indeterminate"></md-progress-bar>
    <div v-if="!isLoading" class="md-layout md-gutter md-alignment-center-center opponentLayout">
      <div class="md-layout-item md-size-25 briscaUser">
        <img class="opponentImage" :src="opponent" />
      </div>
    </div>
    <div v-if="roomStatus" class="table">
      <div class="md-layout md-gutter md-alignment-center-center">

          <transition-group
              name="custom-classes-transition"
              :enter-active-class="getCardTransition(0)"
              :leave-active-class="getRoundWinnerTransition()" tag="div"
              class="md-layout-item md-layout md-alignment-center-center gameTable"
          >

            <div class="md-layout-item md-gutter md-size-5" v-for="(playerCardRound) in roomStatus.round"
                 :key='playerCardRound.player + "_" + playerCardRound.card.suite.name +
                 playerCardRound.card.number'>
              <img class="roundCard"
                   :src='cardImage[playerCardRound.card.suite.name][playerCardRound.card.number]' />
            </div>

          </transition-group>

      </div>
      <div class="md-layout md-gutter md-alignment-center-right">
        <div class="md-layout-item md-size-5">
          <img class="roundCard drivenCard"
               :src='cardImage[roomStatus.drivenCard.suite.name][roomStatus.drivenCard.number]' />
        </div>
        <div class="md-layout-item md-size-5">
          <img class="roundCard"
               :src='backCard' />
        </div>
      </div>
      <BriscaPlayerCards :player-turn="roomStatus.playerTurn" :player-status="playerStatus"
                           v-on:cardClicked="onCardClicked" />
    </div>
  </div>
</template>

<script>
import myService from '@/mixin/service.js'
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";
import BriscaPlayerCards from "../components/brisca/BriscaPlayerCards";

export default {
  name: 'BriscaRoom',
  mixins: [myService],

  components: {
    BriscaPlayerCards,
  },
  props: {
    room: null,
  },
  data() {
    return {
      isLoading: true,
      token: null,

      socket: null,
      stompClient: null,
      connected: false,
      roomStatus: null,
      playerStatus: null,

      numberOfPlayers: 2,
      roundWinner: null,
      opponent: require('@/assets/man.svg'),
      backCard: require('@/assets/cards/BLUE_BACK.svg'),
      cardImage: {
        "oros": {
          1: require('@/assets/cards/AC.svg'),
          2: require('@/assets/cards/2C.svg'),
          3: require('@/assets/cards/3C.svg'),
          4: require('@/assets/cards/4C.svg'),
          5: require('@/assets/cards/5C.svg'),
          6: require('@/assets/cards/6C.svg'),
          7: require('@/assets/cards/7C.svg'),
          8: require('@/assets/cards/JC.svg'),
          9: require('@/assets/cards/QC.svg'),
          10: require('@/assets/cards/KC.svg'),
        },
        "copas": {
          1: require('@/assets/cards/AS.svg'),
          2: require('@/assets/cards/2S.svg'),
          3: require('@/assets/cards/3S.svg'),
          4: require('@/assets/cards/4S.svg'),
          5: require('@/assets/cards/5S.svg'),
          6: require('@/assets/cards/6S.svg'),
          7: require('@/assets/cards/7S.svg'),
          8: require('@/assets/cards/JS.svg'),
          9: require('@/assets/cards/QS.svg'),
          10: require('@/assets/cards/KS.svg'),
        },
        "bastos": {
          1: require('@/assets/cards/AD.svg'),
          2: require('@/assets/cards/2D.svg'),
          3: require('@/assets/cards/3D.svg'),
          4: require('@/assets/cards/4D.svg'),
          5: require('@/assets/cards/5D.svg'),
          6: require('@/assets/cards/6D.svg'),
          7: require('@/assets/cards/7D.svg'),
          8: require('@/assets/cards/JD.svg'),
          9: require('@/assets/cards/QD.svg'),
          10: require('@/assets/cards/KD.svg'),
      },
        "espadas": {
          1: require('@/assets/cards/AH.svg'),
          2: require('@/assets/cards/2H.svg'),
          3: require('@/assets/cards/3H.svg'),
          4: require('@/assets/cards/4H.svg'),
          5: require('@/assets/cards/5H.svg'),
          6: require('@/assets/cards/6H.svg'),
          7: require('@/assets/cards/7H.svg'),
          8: require('@/assets/cards/JH.svg'),
          9: require('@/assets/cards/QH.svg'),
          10: require('@/assets/cards/KH.svg'),
        }
      }
    }
  },
  mounted() {
    this.connect()
  },
  methods: {
    connect: function() {
      this.socket = new SockJS(`http://localhost:8081${this.room.endpoint}`);
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect(
          {},
          () => {
            this.connected = true;
            // subscribe to generic game events
            this.stompClient.subscribe(this.room.subscribeTo, response =>  {
              const body = JSON.parse(response.body)
              if (body.type === "roomStatus") {
                this.roomStatus = body
              } else if (body.type === "cardPlayed") {
                if (!this.roomStatus.round) {
                  this.roomStatus.round = []
                }
                this.roomStatus.round.push({player: body.player, card: body.card})
                // remove player card, TODO check how to remove from opponent card
                if (this.$auth.user && body.player == this.$auth.user.sub) {
                  this.playerStatus.cards = this.playerStatus.cards.filter(it => !this.areEqualCards(it, body.card))
                }
              } else if (body.type == "playerTurnChanged") {
                this.roomStatus.playerTurn = body.playerTurn
              } else if (body.type === "roundWinner") {
                this.roundWinner = body.winner
                // test
                this.roomStatus.round = []
                this.roomStatus.playerTurn = this.roundWinner
              } else {
                console.log("Unknown event", body)
              }

            });
            this.subscribeToPlayerStatus()

            // send request status
            this.sendRequestStatus()
            this.isLoading = false;
          },
          error => {
            console.log(error);
            this.connected = false;
            this.isLoading = false;
          }
      );
    },
    subscribeToPlayerStatus() {
      if (this.$auth.isAuthenticated && this.room.players.includes(this.$auth.user.sub)) {
        this.stompClient.subscribe(this.room.subscribeTo + "/player/" + this.$auth.user.sub, response =>  {
          const body = JSON.parse(response.body)
          if (body.type == "playerStatus") {
            this.playerStatus = body
          } else if (body.type == "cardGiven") {
            this.playerStatus.cards.push(body.card)
          } else {
            console.log("Unknown event", body)
          }
        });
      }
    },
    sendRequestStatus() {
      this.stompClient.send(`/app/game/${this.room.id}`,
          {}, {"X-Player": this.$auth.user.sub});
      this.sendRequestPlayerStatus()
    },
    sendRequestPlayerStatus() {
      if (this.$auth.isAuthenticated && this.room.players.includes(this.$auth.user.sub)) {
        this.stompClient.send(`/app/game/${this.room.id}/player/${this.$auth.user.sub}`,
            {}, {"X-Player": this.$auth.user.sub});
      }
    },
    areEqualCards(card1, card2) {
      return card1.number == card2.number && card1.suite.name == card2.suite.name;
    },
    onCardClicked(card) {
      this.stompClient.send(`/app/game/${this.room.id}/player/${this.$auth.user.sub}/move`,
          JSON.stringify(card), {"X-Player": this.$auth.user.sub});
    },
    getCardImage() {
      return '@/assets/cards/2C.svg'
    },
    getCardTransition(index) {
      if (this.$auth.user.sub && this.roomStatus.players.indexOf(this.$auth.user.sub) == index) {
        return "animate__animated animate__fadeInUp"
      } else {
        return "animate__animated animate__fadeInDown"
      }
    },
    getRoundWinnerTransition() {
      if (this.roundWinner && this.roundWinner == this.$auth.user.sub)
        return "animate__animated animate__fadeOutDown"
      else
        return "animate__animated animate__fadeOutUp"
    },
    isUserTurn() {
      return this.$auth.user.sub && this.roomStatus.playerTurn == this.$auth.user.sub;
    }
  }
}
</script>

<style lang="scss" scoped>
  .table {
    background-color: green;
    border-radius: 10%;
    margin: 0 auto;
    border: 1em solid black;
  }

  .briscaUser {
    text-align: center;
    height:20vh;
  }

  .gameTable {
    height: 20vh;
  }

  .opponentImage {
    height: 20vh;
    width: auto;
  }

  .opponentLayout {
    margin-bottom: 0em;
  }
  .userLayout {
    margin-top: 5em;
  }

  .roundCard {
    height: 5vh;
  }
  .drivenCard {
    transform: rotate(90deg);
  }

</style>