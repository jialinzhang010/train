import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from "axios";
import './assets/js/enums';

const app = createApp(App);

app.use(Antd).use(store).use(router).mount('#app');

const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

axios.interceptors.request.use(function (config) {
    console.log("Request parameters: ", config);
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function(response) {
    console.log("Response: ", response);
    return response;
}, error => {
    console.log("Error: ", error);
    return Promise.reject(error);
});
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log("Environment: ", process.env.NODE_ENV);
console.log("Server: ", process.env.VUE_APP_SERVER);