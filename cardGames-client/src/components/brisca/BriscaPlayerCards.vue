<template>
  <div v-if="playerStatus" class="md-layout md-gutter md-alignment-center-center userLayout">
    <div class="md-layout-item md-size-20 briscaUser"
         v-for="card in playerStatus.cards" :key="card.suite+card.number">
      <UserCard :card="card" :playerTurn="isPlayerTurn()" v-on:cardClicked="onCardClicked" />
    </div>
  </div>
</template>

<script>
  import UserCard from "../UserCard";

  export default {
    name: "BriscaPlayerCards",
    components: {
      UserCard,
    },
    props: {
      playerTurn: String,
      playerStatus: Object,
    },
    methods: {
      isPlayerTurn() {
        return this.$auth.user.sub && this.playerTurn == this.$auth.user.sub;
      },
      onCardClicked(card) {
        console.log("BriscaPlayerCards card clicked", card)
        this.$emit('cardClicked', card)
      }
    }
  }
</script>

<style scoped>

</style>