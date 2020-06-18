<template>
  <div id="app">
    <div class="page-container">
      <md-app md-mode="reveal">
        <md-app-toolbar class="md-primary">
            <div class="md-toolbar-row">
              <div class="md-toolbar-section-start">
                <md-button class="md-icon-button" @click="menuVisible = !menuVisible">
                  <md-icon>menu</md-icon>
                </md-button>
                <span class="md-title">My Title</span>
              </div>

              <div class="md-toolbar-section-end">
                <ToolbarProfile />
              </div>
          </div>
        </md-app-toolbar>

        <md-app-drawer :md-active.sync="menuVisible">
          <md-toolbar class="md-transparent" md-elevation="0">Navigation</md-toolbar>

          <md-list>
            <md-list-item to="/">
              <md-icon>move_to_inbox</md-icon>
              <span class="md-list-item-text">Dashboard</span>
            </md-list-item>

            <md-list-item to="/brisca">
              <md-icon >send</md-icon>
              <span class="md-list-item-text">Brisca</span>
            </md-list-item>

            <md-list-item>
              <md-icon>delete</md-icon>
              <span class="md-list-item-text">Trash</span>
            </md-list-item>

            <md-list-item>
              <md-icon>error</md-icon>
              <span class="md-list-item-text">Spam</span>
            </md-list-item>
          </md-list>
        </md-app-drawer>

        <md-app-content>
          <router-view/>
        </md-app-content>
      </md-app>
    </div>

  </div>
</template>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.md-app-content {
  height: 100vh;
}

</style>

<script>
  import ToolbarProfile from "./components/dashboard/ToolbarProfile";

  export default {
    name: 'Reveal',
    components: {ToolbarProfile},
    data: () => ({
      menuVisible: false
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