import axios from "axios"

// export const API_BASE_URL = "https://managementsystem-byje.onrender.com"
export const API_BASE_URL = "http://localhost:8080"
// export const BASE_URL = "/ManagementSystemFrontend"
export const BASE_URL = ""

const Api =(jwt)=>{
    const api = axios.create({baseURL:API_BASE_URL})
    api.defaults.headers.common["Authorization"] = `Bearer ${jwt}`
    api.defaults.headers.post["Content-Type"] = "application/json"
    return {api}
}



export default Api