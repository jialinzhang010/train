<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainTickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
    </template>
  </a-table>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "daily-train-ticket-view",
  setup() {
    const visible = ref(false);
    let dailyTrainTicket = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      start: undefined,
      startTime: undefined,
      startIndex: undefined,
      end: undefined,
      endTime: undefined,
      endIndex: undefined,
      ydz: undefined,
      ydzPrice: undefined,
      edz: undefined,
      edzPrice: undefined,
      rw: undefined,
      rwPrice: undefined,
      yw: undefined,
      ywPrice: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrainTickets = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
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
      title: 'Departure time',
      dataIndex: 'startTime',
      key: 'startTime',
    },
    {
      title: 'Departure station index ',
      dataIndex: 'startIndex',
      key: 'startIndex',
    },
    {
      title: 'Arrival station',
      dataIndex: 'end',
      key: 'end',
    },
    {
      title: 'Arrival time',
      dataIndex: 'endTime',
      key: 'endTime',
    },
    {
      title: 'Arrival station index ',
      dataIndex: 'endIndex',
      key: 'endIndex',
    },
    {
      title: 'First class remaining tickets',
      dataIndex: 'ydz',
      key: 'ydz',
    },
    {
      title: 'First class ticket price',
      dataIndex: 'ydzPrice',
      key: 'ydzPrice',
    },
    {
      title: 'Second class remaining tickets',
      dataIndex: 'edz',
      key: 'edz',
    },
    {
      title: 'Second class ticket price',
      dataIndex: 'edzPrice',
      key: 'edzPrice',
    },
    {
      title: 'Soft sleeper remaining tickets',
      dataIndex: 'rw',
      key: 'rw',
    },
    {
      title: 'Soft sleeper ticket price',
      dataIndex: 'rwPrice',
      key: 'rwPrice',
    },
    {
      title: 'Hard sleeper remaining tickets',
      dataIndex: 'yw',
      key: 'yw',
    },
    {
      title: 'Hard sleeper ticket price',
      dataIndex: 'ywPrice',
      key: 'ywPrice',
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
      axios.get("/business/admin/daily-train-ticket/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrainTickets.value = data.content.list;
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
      dailyTrainTicket,
      visible,
      dailyTrainTickets,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
    };
  },
});
</script>
