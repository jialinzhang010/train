<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        :openKeys="['batch', 'base', 'business']"
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
      <a-sub-menu key="business">
        <template #title>
          <span>
            <UnorderedListOutlined />
            Manage business
          </span>
        </template>
        <a-menu-item key="/business/daily-train">
          <router-link to="/business/daily-train">
            <coffee-outlined /> &nbsp; Daily train
          </router-link>
        </a-menu-item>
      </a-sub-menu>


      <a-sub-menu key="base">
        <template #title>
          <span>
            <UnorderedListOutlined />
            Train data
          </span>
        </template>
        <a-menu-item key="/base/station">
          <router-link to="/base/station">
            <coffee-outlined /> &nbsp; Stations
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train">
          <router-link to="/base/train">
            <coffee-outlined /> &nbsp; Trains
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train-station">
          <router-link to="/base/train-station">
            <coffee-outlined /> &nbsp; Train Stations
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train-carriage">
          <router-link to="/base/train-carriage">
            <coffee-outlined /> &nbsp; Train Carriages
          </router-link>
        </a-menu-item>

        <a-menu-item key="/base/train-seat">
          <router-link to="/base/train-seat">
            <coffee-outlined /> &nbsp; Train Seats
          </router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="batch">
        <template #title>
          <span>
            <UnorderedListOutlined  />
            Manage batch
          </span>
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <MenuUnfoldOutlined /> &nbsp; Manage jobs
          </router-link>
        </a-menu-item>
      </a-sub-menu>


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
