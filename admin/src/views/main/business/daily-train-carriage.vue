<template>
  <p>
    <a-space>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="Please select a time" />
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">Search</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainCarriages"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'operation'">
        <a-space>
          <a-popconfirm
              title="Deletion is irreversible. Confirm deletion?"
              @confirm="onDelete(record)"
              ok-text="Confirm" cancel-text="Cancel">
            <a style="color: red">Delete</a>
          </a-popconfirm>
          <a @click="onEdit(record)">Edit</a>
        </a-space>
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
  <a-modal v-model:visible="visible" title="Daily train carriage" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="dailyTrainCarriage" :label-col="{span: 7}" :wrapper-col="{ span: 20 }">
      <a-form-item label="date">
        <a-date-picker v-model:value="dailyTrainCarriage.date" valueFormat="YYYY-MM-DD" placeholder="Please select a time" />
      </a-form-item>
      <a-form-item label="Train code">
        <train-select-view v-model="dailyTrainCarriage.trainCode" width="200px"></train-select-view>
      </a-form-item>
      <a-form-item label="Carriage number">
        <a-input v-model:value="dailyTrainCarriage.index" />
      </a-form-item>
      <a-form-item label="Seat type ">
        <a-select v-model:value="dailyTrainCarriage.seatType">
          <a-select-option v-for="item in SEAT_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-form-item>
<!--      <a-form-item label="Seat count">-->
<!--        <a-input v-model:value="dailyTrainCarriage.seatCount" />-->
<!--      </a-form-item>-->
      <a-form-item label="Row count">
        <a-input v-model:value="dailyTrainCarriage.rowCount" />
      </a-form-item>
<!--      <a-form-item label="Column count">-->
<!--        <a-input v-model:value="dailyTrainCarriage.colCount" />-->
<!--      </a-form-item>-->
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";

export default defineComponent({
  name: "daily-train-carriage-view",
  components: {TrainSelectView},
  setup() {
    const SEAT_TYPE_ARRAY = window.SEAT_TYPE_ARRAY;
    const visible = ref(false);
    let dailyTrainCarriage = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      index: undefined,
      seatType: undefined,
      seatCount: undefined,
      rowCount: undefined,
      colCount: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrainCarriages = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      trainCode: null,
      date: null
    });
    const columns = [
    {
      title: 'date',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: 'Train code',
      dataIndex: 'trainCode',
      key: 'trainCode',
    },
    {
      title: 'Carriage number',
      dataIndex: 'index',
      key: 'index',
    },
    {
      title: 'Seat type ',
      dataIndex: 'seatType',
      key: 'seatType',
    },
    {
      title: 'Seat count',
      dataIndex: 'seatCount',
      key: 'seatCount',
    },
    {
      title: 'Row count',
      dataIndex: 'rowCount',
      key: 'rowCount',
    },
    {
      title: 'Column count',
      dataIndex: 'colCount',
      key: 'colCount',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      dailyTrainCarriage.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      dailyTrainCarriage.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/daily-train-carriage/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "Deleted!"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleOk = () => {
      axios.post("/business/admin/daily-train-carriage/save", dailyTrainCarriage.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Saved!"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/daily-train-carriage/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode: params.value.trainCode,
          date: params.value.date
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrainCarriages.value = data.content.list;
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
      SEAT_TYPE_ARRAY,
      dailyTrainCarriage,
      visible,
      dailyTrainCarriages,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      handleOk,
      onEdit,
      onDelete,
      params
    };
  },
});
</script>
