<template>
  <div class="roomInList">
    <md-card>
      <md-card-media>
        <img src="https://vuematerial.io/assets/examples/card-image-1.jpg" alt="People">
      </md-card-media>
      <md-card-header>
        <div class="md-title">{{this.game}}</div>
        <div class="md-subhead">{{this.roomStatus.players.length}}/{{this.roomStatus.numberOfPlayers}} players</div>
      </md-card-header>

      <md-card-expand>
        <md-card-actions md-alignment="space-between">
          <div>
            <md-button :disabled="!$auth.isAuthenticated" v-on:click="joinRoom">Join</md-button>
            <md-button>Join as Spectator</md-button>
          </div>
        </md-card-actions>
      </md-card-expand>
    </md-card>
  </div>
</template>

<script>
  import myService from '@/mixin/service.js'

export default {
  name: 'RoomInList',

  mixins: [myService],

  props: {
    game: String,
    roomStatus: Object
  },
  data() {
    return  {
      isLoading: false
    }
  },
  methods: {
    async joinRoom() {
      const tokenClaims = await this.$auth.getIdTokenClaims();
      const token = tokenClaims.__raw;
      this.isLoading = true;
      this.joinBriscaGame(this.roomStatus.id, token)
        .then(response => {
          this.$emit("roomJoined", response.data)
        }).finally(() => this.isLoading = false)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
  .md-card {
    width: 320px;
    margin: 4px;
    display: inline-block;
    vertical-align: top;
  }
</style>
