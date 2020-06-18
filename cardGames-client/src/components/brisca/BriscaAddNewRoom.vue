<template>
  <div v-if="$auth.isAuthenticated">
    <md-button v-if="!isLoading" v-on:click="this.createGame" class="md-fab md-fab-bottom-right md-primary">
      <md-icon>add</md-icon>
    </md-button>

    <md-snackbar v-if="errorResponse" md-position="center" :md-duration="duration" :md-active.sync="showSnackbar">
      <span>Error when creating new game. Response status: {{errorResponse.status}}</span>
    </md-snackbar>
  </div>
</template>

<script>
  import myService from '@/mixin/service.js'

  export default {
    name: "BriscaAddNewRoom",

    mixins: [myService],

    data () {
      return {
        isLoading: false,

        briscaRoomCreated: null,

        // error handling
        showSnackbar: false,
        errorResponse: null,
        duration: 2000
      }
    },
    methods: {
      async createGame() {
        const tokenClaims = await this.$auth.getIdTokenClaims();
        const token = tokenClaims.__raw;
        this.isLoading = true;
        this.errorResponse = null;
        this.showSnackbar = false;
        this.createBriscaGame(token).then(response => {
          this.briscaRoomCreated = response.data
          console.log("Room created", response)
          this.$emit('roomJoined', this.briscaRoomCreated)
        }).catch(error => {
          this.errorResponse = error.response;
          this.showSnackbar = true;
          this.briscaRoomCreated = null;
        }).finally(() => this.isLoading = false)
      }
    }

  }
</script>

<style scoped>

</style>