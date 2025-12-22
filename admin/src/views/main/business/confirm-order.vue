<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      
    </a-space>
  </p>
  <a-table :dataSource="confirmOrders"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'status'">
        <span v-for="item in CONFIRM_ORDER_STATUS_ARRAY" :key="item.code">
          <span v-if="item.code === record.status">
            {{item.desc}}
          </span>
        </span>
      </template>
    </template>
  </a-table>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "confirm-order-view",
  setup() {
    const CONFIRM_ORDER_STATUS_ARRAY = window.CONFIRM_ORDER_STATUS_ARRAY;
    const visible = ref(false);
    let confirmOrder = ref({
      id: undefined,
      memberId: undefined,
      date: undefined,
      trainCode: undefined,
      start: undefined,
      end: undefined,
      dailyTrainTicketId: undefined,
      tickets: undefined,
      status: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const confirmOrders = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
    {
      title: 'Member id',
      dataIndex: 'memberId',
      key: 'memberId',
    },
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: 'Train code',
      dataIndex: 'trainCode',
      key: 'trainCode',
    },
    {
      title: 'Departure station',
      dataIndex: 'start',
      key: 'start',
    },
    {
      title: 'Arrival station',
      dataIndex: 'end',
      key: 'end',
    },
    {
      title: 'Daily train ticket id',
      dataIndex: 'dailyTrainTicketId',
      key: 'dailyTrainTicketId',
    },
    {
      title: 'tickets',
      dataIndex: 'tickets',
      key: 'tickets',
    },
    {
      title: 'Order status ',
      dataIndex: 'status',
      key: 'status',
    },
    ];


    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/confirm-order/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          confirmOrders.value = data.content.list;
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleTableChange = (page) => {
      pagination.value.pageSize = page.pageSize;
      handleQuery({
        page: page.current,
        size: page.pageSize
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      CONFIRM_ORDER_STATUS_ARRAY,
      confirmOrder,
      visible,
      confirmOrders,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
    };
  },
});
</script>
