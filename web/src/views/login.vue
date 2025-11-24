<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center">
        <rocket-two-tone/>&nbsp;Train Ticketing System
      </h1>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[{ required: true, message: 'Please enter phone number!' }]"
        >
          <a-input v-model:value="loginForm.mobile" placeholder="phone number"/>
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[{ required: true, message: 'Please enter verification code!' }]"
        >
          <a-input v-model:value="loginForm.code">
            <template #addonAfter>
              <a @click="sendCode">Send code</a>
            </template>
          </a-input>
          <!--<a-input v-model:value="loginForm.code" placeholder="Verification code"/>-->
        </a-form-item>
        <a-form-item>
          <a-button type="primary" block @Click="login">Login</a-button>
        </a-form-item>
      </a-form>
    </a-col>
  </a-row>
</template>

<script>
import {defineComponent, reactive} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "login-view",
  setup() {
    const loginForm = reactive({
      mobile: '13000000000',
      code: '',
    });

    const sendCode = () => {
      axios.post("http://localhost:8000/member/member/send-code", {
        mobile: loginForm.mobile
      }).then(response => {
        console.log(response);
        let data = response.data;
        if (data.success) {
          notification.success({ description: "Verification code sent successfully!"});
          loginForm.code = "8888";
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const login = () => {
      axios.post("http://localhost:8000/member/member/login", loginForm)
          .then(response => {
            let data = response.data;
            if (data.success) {
              notification.success({ description: "Login successfully!" });
              console.log("Login successfully: ", data.content);
            } else {
              notification.error({ description: data.message });
            }
          });
    }

    return {
      loginForm,
      sendCode,
      login
    };
  },
});
</script>


<style>
login-main h1 {
  font-size: 25px;
  font-weight: bold;
}

.login-main {
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}
</style>