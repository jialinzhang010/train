<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrains"
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
      <template v-else-if="column.dataIndex === 'type'">
        <span v-for="item in TRAIN_TYPE_ARRAY" :key="item.code">
          <span v-if="item.code === record.type">
            {{item.desc}}
          </span>
        </span>
      </template>
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Daily train code" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="dailyTrain" :label-col="{span: 7}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Date">
        <a-date-picker v-model:value="dailyTrain.date" valueFormat="YYYY-MM-DD" placeholder="Please select a time" />
      </a-form-item>
      <a-form-item label="Train code">
        <train-select-view v-model="dailyTrain.code" @change="onChangeCode"></train-select-view>
      </a-form-item>
      <a-form-item label="Train Type">
        <a-select v-model:value="dailyTrain.type">
          <a-select-option v-for="item in TRAIN_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Departure station">
        <a-input v-model:value="dailyTrain.start" />
      </a-form-item>
      <a-form-item label="Departure time">
        <a-time-picker v-model:value="dailyTrain.startTime" valueFormat="HH:mm:ss" placeholder="Please select a time" />
      </a-form-item>
      <a-form-item label="Terminal station">
        <a-input v-model:value="dailyTrain.end" />
      </a-form-item>
      <a-form-item label="Arrival time">
        <a-time-picker v-model:value="dailyTrain.endTime" valueFormat="HH:mm:ss" placeholder="Please select a time" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";

export default defineComponent({
  name: "daily-train-view",
  components: {TrainSelectView},
  setup() {
    const TRAIN_TYPE_ARRAY = window.TRAIN_TYPE_ARRAY;
    const visible = ref(false);
    let dailyTrain = ref({
      id: undefined,
      date: undefined,
      code: undefined,
      type: undefined,
      start: undefined,
      startTime: undefined,
      end: undefined,
      endTime: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrains = ref([]);
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
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: 'Train Type ',
      dataIndex: 'type',
      key: 'type',
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
      title: 'Terminal station',
      dataIndex: 'end',
      key: 'end',
    },
    {
      title: 'Arrival time',
      dataIndex: 'endTime',
      key: 'endTime',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      dailyTrain.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      dailyTrain.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/daily-train/delete/" + record.id).then((response) => {
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
      axios.post("/business/admin/daily-train/save", dailyTrain.value).then((response) => {
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
      axios.get("/business/admin/daily-train/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrains.value = data.content.list;
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

    const onChangeCode = (train) => {
      console.log("Train code dropdown selected: ", train);
      let t = Tool.copy(train);
      delete t.id;
      // Use assign to merge
      dailyTrain.value = Object.assign(dailyTrain.value, t);
    }

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      TRAIN_TYPE_ARRAY,
      dailyTrain,
      visible,
      dailyTrains,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      handleOk,
      onEdit,
      onDelete,
      onChangeCode
    };
  },
});
</script>
