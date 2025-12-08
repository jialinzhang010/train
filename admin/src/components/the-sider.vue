<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; Welcome
        </router-link>
      </a-menu-item>

      <a-menu-item key="/about">
        <router-link to="/about">
          <coffee-outlined /> &nbsp; About
        </router-link>
      </a-menu-item>

      <a-menu-item key="/station">
        <router-link to="/station">
          <coffee-outlined /> &nbsp; Manage Stations
        </router-link>
      </a-menu-item>

      <a-menu-item key="/train">
        <router-link to="/train">
          <coffee-outlined /> &nbsp; Manage Trains
        </router-link>
      </a-menu-item>

      <a-menu-item key="/train-station">
        <router-link to="/train-station">
          <coffee-outlined /> &nbsp; Manage Train Stations
        </router-link>
      </a-menu-item>

    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from "@/router";

export default defineComponent({
  name: "the-sider-view",
  setup() {
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
