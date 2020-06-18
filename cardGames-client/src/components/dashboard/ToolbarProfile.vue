<template>
  <div class="toolbarProfile">
    <md-button class="md-icon-button" v-if="!$auth.isAuthenticated" @click="login">
      <md-icon>person</md-icon>
    </md-button>

    <div>
      <md-avatar class="md-avatar-icon md-primary" v-if="$auth.isAuthenticated">
        <img :src="$auth.user.picture" alt="Avatar">
      </md-avatar>
      <md-menu md-direction="top-start" v-if="$auth.isAuthenticated">
        <md-button class="md-icon-button" md-menu-trigger>
          <md-icon>more_vert</md-icon>
        </md-button>
        <md-menu-content>
          <md-menu-item @click="logout">Logout</md-menu-item>
        </md-menu-content>
      </md-menu>
    </div>

  </div>
</template>

<script>
  export default {
    name: "ToolbarProfile",
    data: () => ({
    }),
    methods: {
      // Log the user in
      login() {
        this.$auth.loginWithRedirect();
      },
      // Log the user out
      logout() {
        this.$auth.logout({
          returnTo: window.location.origin
        });
      }
    }

  }
</script>

<style scoped>

</style>