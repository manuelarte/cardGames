<template>
  <md-card md-with-hover >
    <md-ripple>
      <div v-on:click="emitEvent">
        <md-card-content>
          <img class="roundCard" v-bind:class="{ 'blink-image': playerTurn }"
               :src='cardImage[card.suite.name][card.number]' />
        </md-card-content>
      </div>
    </md-ripple>
  </md-card>
</template>

<script>
  export default {
    name: "UserCard",
    props: {
      card: Object,
      playerTurn: Boolean,
    },
    data() {
      return {
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
    methods: {
      emitEvent() {
        this.$emit('cardClicked', this.card)
      },
      getCardImage() {
        let suiteStringValue = "C"
        switch (this.card.suite.toLowerCase()) {
          case "oros":
            suiteStringValue = "C"
            break;
          case "copas":
            suiteStringValue = "D"
            break;
          case "bastos":
            suiteStringValue = "H"
            break;
          case "espadas":
            suiteStringValue = "S"
            break;
          default:
            // code block
            break;
        }
        return require(`@/assets/cards/${this.card.number}${suiteStringValue}.svg`)
      }
    }
  }
</script>

<style scoped>
.md-card {
  height: 100%;
}
/* Firefox old*/
@-moz-keyframes blink {
  0% {
    opacity:1;
  }
  50% {
    opacity:0;
  }
  100% {
    opacity:1;
  }
}

@-webkit-keyframes blink {
  0% {
    opacity:1;
  }
  50% {
    opacity:0;
  }
  100% {
    opacity:1;
  }
}
/* IE */
@-ms-keyframes blink {
  0% {
    opacity:1;
  }
  50% {
    opacity:0;
  }
  100% {
    opacity:1;
  }
}
/* Opera and prob css3 final iteration */
@keyframes blink {
  0% {
    opacity:1;
  }
  50% {
    opacity:0.3;
  }
  100% {
    opacity:1;
  }
}
.blink-image {
  -moz-animation: blink normal 2s infinite ease-in-out; /* Firefox */
  -webkit-animation: blink normal 2s infinite ease-in-out; /* Webkit */
  -ms-animation: blink normal 2s infinite ease-in-out; /* IE */
  animation: blink normal 2s infinite ease-in-out; /* Opera and prob css3 final iteration */
}
</style>