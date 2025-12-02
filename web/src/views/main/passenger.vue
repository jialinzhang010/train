<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="onAdd">Add</a-button>
    </a-space>
  </p>
    <a-table :dataSource="passengers"
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

    <a-modal v-model:visible="visible" title="Passenger" @ok="handleOk">
      <a-form :model="passenger" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
        <a-form-item label="Name">
          <a-input v-model:value="passenger.name" />
        </a-form-item>
        <a-form-item label="ID">
          <a-input v-model:value="passenger.idCard" />
        </a-form-item>
        <a-form-item label="Type">
            <a-select v-model:value="passenger.type">
              <a-select-option value="1">Adult</a-select-option>
              <a-select-option value="2">Child</a-select-option>
              <a-select-option value="3">Student</a-select-option>
            </a-select>
        </a-form-item>
      </a-form>
      </a-modal>
</template>

<script>
import {defineComponent, ref, onMounted} from "vue";
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  setup() {
    const visible = ref(false);
    const passenger = ref({
      id: undefined,
      memberId: undefined,
      name: undefined,
      idCard: undefined,
      type: undefined,
      createTime: undefined,
      updateTime: undefined,
    });

    const passengers = ref([]);

    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 2,
    });

    let loading = ref(false);

    const columns = [{
      title: 'Name',
      dataIndex: 'name',
      key: 'name'
    }, {
      title: 'ID',
      dataIndex: 'idCard',
      key: 'idCard'
    }, {
      title: 'Type',
      dataIndex: 'type',
      key: 'type'
    }, {
      title: 'Operation',
      dataIndex: 'operation'
    }];

    const onAdd = () => {
      passenger.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      passenger.value = window.Tool.copy(record);
      visible.value = true;
    }

    const handleOk = e => {
      console.log(e);
      axios.post("/member/passenger/save", passenger.value).then((response) => {
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
      })
    };

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          size: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/member/passenger/query-list", {
        params: {
          page: param.page,
          size: param.size
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          passengers.value = data.content.list;
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({description: data.message})
        }
      })
    }

    const handleTableChange = (pagination) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    const onDelete = (record) => {
      axios.delete("/member/passenger/delete/" + record.id).then((response) => {
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

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      visible,
      onAdd,
      onEdit,
      handleOk,
      onDelete,
      passengers,
      columns,
      passenger,
      pagination,
      handleTableChange,
      handleQuery,
      loading
    };
  }
})
</script>
<style>

</style>