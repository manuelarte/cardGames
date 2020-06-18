<template>
  <div class="briscaMain">
    <BriscaAddNewRoom v-on:roomJoined="roomJoined" />
    <md-tabs>
      <md-tab id="tab-home" md-label="Brisca Games" exact>
        <RoomList v-on:roomJoined="roomJoined" />
      </md-tab>
      <md-tab :id="`tab-game-${roomStatus.id}`" :md-label="'Brisca Game ' + (index+1)" v-for="(roomStatus, index) in joinedBriscaRooms"
              :key="'tab-' + roomStatus.id">
        <BriscaRoom :room="roomStatus" />
      </md-tab>
    </md-tabs>
  </div>
</template>

<script>
import BriscaAddNewRoom from '@/components/brisca/BriscaAddNewRoom.vue'
import RoomList from '@/components/RoomList.vue'
import BriscaRoom from "./BriscaRoom";

export default {
  name: 'BriscaMain',
  components: {
    BriscaRoom,
    BriscaAddNewRoom,
    RoomList
  },
  data() {
    return {
      joinedBriscaRooms: []
    }
  },
  methods: {
    roomJoined(event) {
      console.log(event)
      this.joinedBriscaRooms.push(event);
      // update room list
    }
  }
}
</script>

<style scoped>
  .md-tab {
    min-height: 500px;
  }
</style>
