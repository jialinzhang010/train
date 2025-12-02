<template>
  <p>
    <a-space>
      <a-button type="primary" @click="handleQuery()">Refresh</a-button>
      <a-button type="primary" @click="showModal">Add</a-button>
    </a-space>
  </p>
    <a-table :dataSource="passengers"
             :columns="columns"
             :pagination="pagination"
             @change="handleTableChange"
             :loading="loading"/>
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
import {defineComponent, reactive, ref, onMounted} from "vue";
// import passenger from "@/views/main/passenger.vue";
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  setup() {
    const visible = ref(false);
    const passenger = reactive({
      id: undefined,
      memberId: undefined,
      name: undefined,
      idCard: undefined,
      type: undefined,
      createTime: undefined,
      updateTime: undefined,
    });

    const passengers = ref([]);

    const pagination = reactive({
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
    }];

    const showModal = () => {
      visible.value = true;
    };

    const handleOk = e => {
      console.log(e);
      axios.post("/member/passenger/save", passenger).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "Saved!"});
          visible.value = false;
          handleQuery({
            page: pagination.current,
            size: pagination.pageSize
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
          size: pagination.pageSize
        };
      };
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
          pagination.current = param.page;
          pagination.total = data.content.total;
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

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.pageSize
      });
    });

    return {
      visible,
      showModal,
      handleOk,
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