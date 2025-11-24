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
          <!--<a-input v-model:value="loginForm.code" placeholder="验证码"/>-->
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

export default defineComponent({
  name: "login-view",
  setup() {
    const loginForm = reactive({
      mobile: '13000000000',
      code: '',
    });
    const onFinish = values => {
      console.log("Success: ", values);
    };
    const onFinishFailed = errorInfo => {
      console.log('Failed: ', errorInfo);
    };
    return {
      loginForm,
      onFinish,
      onFinishFailed,
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