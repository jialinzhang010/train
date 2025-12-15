<template>
  <p>
    <a-space>
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">Search</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="trainStations"
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
    </template>
  </a-table>
  <a-modal v-model:visible="visible" title="Train station" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="trainStation" :label-col="{span: 6}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Train code">
        <train-select-view v-model="trainStation.trainCode"></train-select-view>
      </a-form-item>
      <a-form-item label="Station order">
        <a-input v-model:value="trainStation.index" />
      </a-form-item>
      <a-form-item label="Station name">
        <station-select-view v-model:model-value="trainStation.name"></station-select-view>
      </a-form-item>
      <a-form-item label="Arrival time">
        <a-time-picker v-model:value="trainStation.inTime" valueFormat="HH:mm:ss" placeholder="Please select a time" />
      </a-form-item>
      <a-form-item label="Departure time">
        <a-time-picker v-model:value="trainStation.outTime" valueFormat="HH:mm:ss" placeholder="Please select a time" />
      </a-form-item>
      <a-form-item label="Dwell time">
        <a-time-picker v-model:value="trainStation.stopTime" valueFormat="HH:mm:ss" placeholder="Please select a time" disabled/>
      </a-form-item>
      <a-form-item label="Distance">
        <a-input v-model:value="trainStation.km" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import {defineComponent, ref, onMounted, watch} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import StationSelectView from "@/components/station-select.vue";
import dayjs from 'dayjs';

export default defineComponent({
  name: "train-station-view",
  components: {StationSelectView, TrainSelectView},
  setup() {
    const visible = ref(false);
    let trainStation = ref({
      id: undefined,
      trainCode: undefined,
      index: undefined,
      name: undefined,
      inTime: undefined,
      outTime: undefined,
      stopTime: undefined,
      km: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const trainStations = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      trainCode: null
    });
    const columns = [
    {
      title: 'Train code',
      dataIndex: 'trainCode',
      key: 'trainCode',
    },
    {
      title: 'Station order',
      dataIndex: 'index',
      key: 'index',
    },
    {
      title: 'Station name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Arrival time',
      dataIndex: 'inTime',
      key: 'inTime',
    },
    {
      title: 'Departure time',
      dataIndex: 'outTime',
      key: 'outTime',
    },
    {
      title: 'Dwell time',
      dataIndex: 'stopTime',
      key: 'stopTime',
    },
    {
      title: 'Distance',
      dataIndex: 'km',
      key: 'km',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    watch(() => trainStation.value.inTime, () => {
      let diff = dayjs(trainStation.value.outTime, 'HH:mm:ss').diff(dayjs(trainStation.value.inTime, 'HH:mm:ss'), 'seconds');
      trainStation.value.stopTime = dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    }, {immediate: true});

    watch(() => trainStation.value.outTime, () => {
      let diff = dayjs(trainStation.value.outTime, 'HH:mm:ss').diff(dayjs(trainStation.value.inTime, 'HH:mm:ss'), 'seconds');
      trainStation.value.stopTime = dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    }, {immediate: true});

    const onAdd = () => {
      trainStation.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      trainStation.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/train-station/delete/" + record.id).then((response) => {
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
      axios.post("/business/admin/train-station/save", trainStation.value).then((response) => {
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
      axios.get("/business/admin/train-station/query-list", {
        params: {
          page: param.page,
          size: param.size,
          trainCode: params.value.trainCode
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          trainStations.value = data.content.list;
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
      trainStation,
      visible,
      trainStations,
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
