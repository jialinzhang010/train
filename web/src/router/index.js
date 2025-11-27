import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
  {
    path: '/login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/',
    component: () => import('../views/main.vue'),
    meta: {
      requireLogin: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(function(item) {
    console.log(item, "Require login verification or not: ", item.meta.requireLogin || false);
    return item.meta.requireLogin;
  })) {
    const _member = store.state.member;
    console.log("Start login verification: ", _member);
    if (!_member.token) {
      console.log("User did not log in or session expired!");
      notification.error({ description: "Not logged in or session expired" });
      next("/login");
    } else {
      next();
    }
  } else {
    next();
  }
})

export default router
