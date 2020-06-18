<template>
  <div class="roomList md-layout md-gutter md-alignment-center">

    <div v-if="rooms && rooms.length == 0">
      No rooms available
    </div>
    <div class="" v-for="roomStatus in rooms" :key="'list-' + roomStatus.id">
      <RoomInList game="Brisca" :roomStatus="roomStatus" v-on:roomJoined="onRoomJoined"/>
    </div>
  </div>
</template>

<script>
import RoomInList from "./RoomInList";
import myService from '@/mixin/service.js'

export default {
  name: 'RoomList',

  mixins: [myService],

  components: {RoomInList},
  data() {
    return {
      isLoading: true,
      rooms: null
    }
  },
  mounted() {
    this.getRoomList()
  },
  methods: {
    getRoomList() {
      this.loadBriscaGames().then(response => {
        this.rooms = response.data.content
        // go for each room and add the ones the user is already joined
        if (this.$auth.isAuthenticated) {
          const userId = this.$auth.user.sub
          this.rooms.forEach(room => {
            if (room.players.includes(userId)) {
              this.onRoomJoined(room)
            }
          })
        }
      }).finally(() => this.isLoading = false)
    },
    onRoomJoined(room) {
      this.$emit("roomJoined", room)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
</style>
