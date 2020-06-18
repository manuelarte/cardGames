import axios from 'axios';

const backendUrl = process.env.VUE_APP_CLIENT_URL

export default {

  methods: {
   createBriscaGame: function (token) {
      return axios
      .post(backendUrl + "/api/v1/games/brisca", null, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
    },
    joinBriscaGame: function (id, token) {
      return axios
      .post(`${backendUrl}/api/v1/games/brisca/${id}`, null, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
    },
    loadBriscaGames: function() {
      return axios.get(`${backendUrl}/api/v1/games/brisca/`, {})
    }
  }
}