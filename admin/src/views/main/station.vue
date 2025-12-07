<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
  <a-table :dataSource="stations"
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
  <a-modal v-model:visible="visible" title="Station" @ok="handleOk"
           ok-text="Confirm" cancel-text="Cancel">
    <a-form :model="station" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="Station name">
        <a-input v-model:value="station.name" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";

export default defineComponent({
  name: "station-view",
  setup() {
    const visible = ref(false);
    let station = ref({
      id: undefined,
      name: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const stations = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const columns = [
    {
      title: 'Station name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Operation',
      dataIndex: 'operation'
    }
    ];

    const onAdd = () => {
      station.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      station.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/station/delete/" + record.id).then((response) => {
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
      axios.post("/business/station/save", station.value).then((response) => {
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
      axios.get("/business/station/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          stations.value = data.content.list;
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
      station,
      visible,
      stations,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      onAdd,
      handleOk,
      onEdit,
      onDelete
    };
  },
});
</script>
