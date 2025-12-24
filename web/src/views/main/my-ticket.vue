<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      
    </a-space>
  </p>
  <a-table :dataSource="tickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
      </template>
      <template v-else-if="column.dataIndex === 'col'">
        <span v-for="item in SEAT_COL_ARRAY" :key="item.code">
          <span v-if="item.code === record.col && item.type === record.seatType">
            {{item.desc}}
          </span>
        </span>
      </template>
      <template v-else-if="column.dataIndex === 'seatType'">
        <span v-for="item in SEAT_TYPE_ARRAY" :key="item.code">
          <span v-if="item.code === record.seatType">
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
  name: "ticket-view",
  setup() {
    const SEAT_COL_ARRAY = window.SEAT_COL_ARRAY;
    const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
    const visible = ref(false);
    let ticket = ref({
      id: undefined,
      memberId: undefined,
      passengerId: undefined,
      passengerName: undefined,
      date: undefined,
      trainCode: undefined,
      carriageIndex: undefined,
      row: undefined,
      col: undefined,
      start: undefined,
      startTime: undefined,
      end: undefined,
      endTime: undefined,
      seatType: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const tickets = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
    {
      title: 'Passenger name',
      dataIndex: 'passengerName',
      key: 'passengerName',
    },
    {
      title: 'Date',
      dataIndex: 'trainDate',
      key: 'date',
    },
    {
      title: 'Train code',
      dataIndex: 'trainCode',
      key: 'trainCode',
    },
    {
      title: 'Carriage index',
      dataIndex: 'carriageIndex',
      key: 'carriageIndex',
    },
    {
      title: 'Row',
      dataIndex: 'seatRow',
      key: 'row',
    },
    {
      title: 'Col',
      dataIndex: 'seatCol',
      key: 'col',
    },
    {
      title: 'Departure station',
      dataIndex: 'startStation',
      key: 'start',
    },
    {
      title: 'Departure time',
      dataIndex: 'startTime',
      key: 'startTime',
    },
    {
      title: 'Arrival station',
      dataIndex: 'endStation',
      key: 'end',
    },
    {
      title: 'Arrival time',
      dataIndex: 'endTime',
      key: 'endTime',
    },
    {
      title: 'Seat type',
      dataIndex: 'seatType',
      key: 'seatType',
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
      axios.get("/member/ticket/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          tickets.value = data.content.list;
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
      SEAT_COL_ARRAY,
      SEAT_TYPE_ARRAY,
      ticket,
      visible,
      tickets,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
    };
  },
});
</script>
