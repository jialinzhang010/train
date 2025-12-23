<template>
  <div class="order-train">
  <span class="order-train-main">{{dailyTrainTicket.date}}</span>&nbsp;
  <span class="order-train-main">{{dailyTrainTicket.trainCode}}</span>&nbsp;
  <span class="order-train-main">{{dailyTrainTicket.start}}</span>&nbsp;
  <span class="order-train-main">{{dailyTrainTicket.startTime}}</span>&nbsp;
  <span class="order-train-main">--</span>&nbsp;
  <span class="order-train-main">{{dailyTrainTicket.end}}</span>&nbsp;
  <span class="order-train-main">{{dailyTrainTicket.endTime}}</span>&nbsp;
    <div class="order-train-ticket">
      <span v-for="item in seatTypes" :key="item.type">
        <span>{{item.desc}}: </span>
        <span class="order-train-ticket-main">{{item.price}}¥</span>&nbsp;
        <span class="order-train-ticket-main">{{item.count}}</span>&nbsp;tickets&nbsp;&nbsp;
      </span>
    </div>
  </div>
  <a-divider></a-divider>
  <b>Check passengers: </b>&nbsp;
  <a-checkbox-group v-model:value="passengerChecks" :options="passengerOptions" />
  <div class="order-tickets">
    <a-row class="order-tickets-header" v-if="tickets.length > 0">
      <a-col :span="2">Name</a-col>
      <a-col :span="6">ID</a-col>
      <a-col :span="4">Type</a-col>
      <a-col :span="4">Seat</a-col>
    </a-row>
    <a-row class="order-tickets-row" v-for="ticket in tickets" :key="ticket.passengerId">
      <a-col :span="2">{{ticket.passengerName}}</a-col>
      <a-col :span="6">{{ticket.passengerIdCard}}</a-col>
      <a-col :span="4">
        <a-select v-model:value="ticket.passengerType" style="width: 100%">
          <a-select-option v-for="item in PASSENGER_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="4">
        <a-select v-model:value="ticket.seatTypeCode" style="width: 100%">
          <a-select-option v-for="item in seatTypes" :key="item.code" :value="item.code">
            {{item.desc}}
          </a-select-option>
        </a-select>
      </a-col>
    </a-row>
  </div>

  <div v-if="tickets.length > 0">
    <a-button type="primary" size="large" @click="finishCheckPassenger">Submit order</a-button>
  </div>

  <a-modal v-model:visible="visible" title="Please verify the information"
           style="top: 50px; width: 800px"
           ok-text="Confirm" cancel-text="Cancel"
           @ok="handleOk"
           >
    <div class="order-tickets">
      <a-row class="order-tickets-header" v-if="tickets.length > 0">
        <a-col :span="5">Passenger</a-col>
        <a-col :span="10">ID</a-col>
        <a-col :span="3">Type</a-col>
        <a-col :span="5">Seat</a-col>
      </a-row>
      <a-row class="order-tickets-row" v-for="ticket in tickets" :key="ticket.passengerId">
        <a-col :span="5">{{ticket.passengerName}}</a-col>
        <a-col :span="10">{{ticket.passengerIdCard}}</a-col>
        <a-col :span="3">
          <span v-for="item in PASSENGER_TYPE_ARRAY" :key="item.code">
            <span v-if="item.code === ticket.passengerType">
              {{item.desc}}
            </span>
          </span>
        </a-col>
        <a-col :span="5">
          <span v-for="item in seatTypes" :key="item.code">
            <span v-if="item.code === ticket.seatTypeCode">
              {{item.desc}}
            </span>
          </span>
        </a-col>
      </a-row>
      <br/>
      <div v-if="chooseSeatType === 0" style="color: red;">
        Manual seat selection is not supported for this order.
        <div>Note: Seat selection is only available for all First-Class or all Second-Class tickets.</div>
        <div>Note: Seat selection is disabled when remaining tickets are low (less than 20).</div>
      </div>
      <div v-else style="text-align: center">
        <a-switch class="choose-seat-item" v-for="item in SEAT_COL_ARRAY" :key="item.code"
                  v-model:checked="chooseSeatObj[item.code + '1']"
                  :checked-children="item.desc" :un-checked-children="item.desc" />

        <div v-if="tickets.length > 1">
          <a-switch class="choose-seat-item" v-for="item in SEAT_COL_ARRAY" :key="item.code"
                    v-model:checked="chooseSeatObj[item.code + '2']"
                    :checked-children="item.desc" :un-checked-children="item.desc" />
        </div>

        <div style="color: #999999; margin-top: 10px;">
          Tip: You can select up to {{tickets.length}} seat(s).
        </div>
        <br>
        最终购票：{{tickets}}
      </div>
    </div>
  </a-modal>

</template>
<script>
import {computed, defineComponent, onMounted, ref, watch} from "vue";
import {notification} from "ant-design-vue";
import axios from "axios";
import passenger from "@/views/main/passenger.vue";

export default defineComponent({
  name: "order-view",
  setup() {
    const passengers = ref([]);
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    console.log("order info", dailyTrainTicket);
    const SEAT_TYPE = window.SEAT_TYPE;
    console.log(SEAT_TYPE);
    const seatTypes = [];
    const passengerOptions = ref([]);
    const passengerChecks = ref([]);
    for (let KEY in SEAT_TYPE) {
      let key = KEY.toLowerCase();
      if (dailyTrainTicket[key] >= 0) {
        seatTypes.push({
          type: KEY,
          code: SEAT_TYPE[KEY]["code"],
          desc: SEAT_TYPE[KEY]["desc"],
          count: dailyTrainTicket[key],
          price: dailyTrainTicket[key + 'Price'],
        })
      }
    }
    const tickets = ref([]);
    const PASSENGER_TYPE_ARRAY = window.PASSENGER_TYPE_ARRAY;
    const visible = ref(false);
    watch(() => passengerChecks.value, (newVal, oldVal) => {
      console.log("Checked passengers changed: ", newVal, oldVal);
      tickets.value = [];
      passengerChecks.value.forEach((item) => tickets.value.push({
        passengerId: item.id,
        passengerType: item.type,
        seatTypeCode: seatTypes[0].code,
        passengerName: item.name,
        passengerIdCard: item.idCard
      }))
    }, {immediate: true});

    const chooseSeatType = ref(0);
    // 根据选择的座位类型，计算出对应的列，比如要选的是一等座，就筛选出ACDF，要选的是二等座，就筛选出ABCDF
    const SEAT_COL_ARRAY = computed(() => {
      return window.SEAT_COL_ARRAY.filter(item => item.type === chooseSeatType.value);
    });
    console.log(chooseSeatType)
    // 选择的座位
    // {
    //   A1: false, C1: true，D1: false, F1: false，
    //   A2: false, C2: false，D2: true, F2: false
    // }
    const chooseSeatObj = ref({});
    watch(() => SEAT_COL_ARRAY.value, () => {
      chooseSeatObj.value = {};
      for (let i = 1; i <= 2; i++) {
        SEAT_COL_ARRAY.value.forEach((item) => {
          chooseSeatObj.value[item.code + i] = false;
        })
      }
    }, {immediate: true});


    const handleQueryPassenger = () => {
      axios.get("/member/passenger/query-mine").then((response) => {
        let data = response.data;
        if (data.success) {
          passengers.value = data.content;
          passengers.value.forEach((item) => passengerOptions.value.push({
            label: item.name,
            value: item
          }))
        } else {
          notification.error({description: data.message});
        }
      })
    }

    const finishCheckPassenger = () => {
      console.log("Ticket List: ", tickets.value);

      // 1. Validate max tickets allowed
      if (tickets.value.length > 5) {
        notification.error({ description: 'You can purchase a maximum of 5 tickets per order.' });
        return;
      }

      /**
       * 2. Validate ticket availability.
       * Check if the inventory is sufficient for each selected seat type.
       * Note: Front-end validation reduces server pressure.
       * We use a deep copy (seatTypesTemp) to simulate deduction to avoid affecting the original reactive data.
       */
      let seatTypesTemp = Tool.copy(seatTypes);
      for (let i = 0; i < tickets.value.length; i++) {
        let ticket = tickets.value[i];
        for (let j = 0; j < seatTypesTemp.length; j++) {
          let seatType = seatTypesTemp[j];
          // Simulate inventory deduction for validation
          if (ticket.seatTypeCode === seatType.code) {
            seatType.count--;
            if (seatType.count < 0) {
              notification.error({ description: `Insufficient inventory for ${seatType.desc}` });
              return;
            }
          }
        }
      }
      console.log("Front-end availability check passed.");

      /**
       * 3. Determine if manual seat selection is supported.
       * Seat selection is only available for pure First-Class (YDZ) or Second-Class (EDZ) orders.
       */
      let ticketSeatTypeCodes = [];
      for (let i = 0; i < tickets.value.length; i++) {
        let ticket = tickets.value[i];
        ticketSeatTypeCodes.push(ticket.seatTypeCode);
      }

      // De-duplicate selected seat types: e.g., [1, 1, 2] -> [1, 2]
      const ticketSeatTypeCodesSet = Array.from(new Set(ticketSeatTypeCodes));
      console.log("Selected seat type codes: ", ticketSeatTypeCodesSet);

      if (ticketSeatTypeCodesSet.length !== 1) {
        console.log("Multiple seat types selected; manual seat selection is disabled.");
        chooseSeatType.value = 0;
      } else {
        // Only one type of seat is selected
        const selectedTypeCode = ticketSeatTypeCodesSet[0];
        if (selectedTypeCode === SEAT_TYPE.YDZ.code) {
          console.log("First-class seat selection enabled.");
          chooseSeatType.value = SEAT_TYPE.YDZ.code;
        } else if (selectedTypeCode === SEAT_TYPE.EDZ.code) {
          console.log("Second-class seat selection enabled.");
          chooseSeatType.value = SEAT_TYPE.EDZ.code;
        } else {
          console.log("Seat type does not support manual selection.");
          chooseSeatType.value = 0;
        }

        /**
         * 4. Disable seat selection if inventory is low (threshold: 20).
         * Low availability decreases the success rate of manual seat selection.
         */
        if (chooseSeatType.value !== 0) {
          for (let i = 0; i < seatTypes.length; i++) {
            let seatType = seatTypes[i];
            if (selectedTypeCode === seatType.code) {
              if (seatType.count < 20) {
                console.log("Inventory below 20; manual seat selection disabled for better success rate.");
                chooseSeatType.value = 0;
                break;
              }
            }
          }
        }
      }

      // 5. Display confirmation modal
      visible.value = true;
    };

    const handleOk = () => {
      console.log("Selected seats: ", chooseSeatObj.value);

// Set the seat for each ticket.
// First, clear any previously assigned seats. This handles cases where
// a user re-selects seats after a previous validation failure.
      for (let i = 0; i < tickets.value.length; i++) {
        tickets.value[i].seat = null;
      }

      let seatIndex = -1;
// Validation logic: The number of selected seats must either be zero (no selection)
// or exactly match the number of tickets (seatIndex === tickets.value.length - 1).
      for (let key in chooseSeatObj.value) {
        if (chooseSeatObj.value[key]) {
          seatIndex++;
          if (seatIndex > tickets.value.length - 1) {
            notification.error({ description: 'Number of selected seats exceeds the number of tickets.' });
            return;
          }
          tickets.value[seatIndex].seat = key;
        }
      }

      if (seatIndex > -1 && seatIndex < (tickets.value.length - 1)) {
        notification.error({ description: 'Please select seats for all passengers.' });
        return;
      }

      console.log("最终购票：", tickets.value);

      axios.post("/business/confirm-order/do", {
        dailyTrainTicketId: dailyTrainTicket.id,
        date: dailyTrainTicket.date,
        trainCode: dailyTrainTicket.trainCode,
        start: dailyTrainTicket.start,
        end: dailyTrainTicket.end,
        tickets: tickets.value,
        // imageCodeToken: imageCodeToken.value,
        // imageCode: imageCode.value,
        // lineNumber: lineNumber.value
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "下单成功！"});
          // visible.value = false;
          // imageCodeModalVisible.value = false;
          // lineModalVisible.value = true;
          // confirmOrderId.value = data.content;
          // queryLineCount();
        } else {
          notification.error({description: data.message});
        }
      });
    }

    onMounted(() => {
      handleQueryPassenger();
    });

    return {
      dailyTrainTicket,
      seatTypes,
      passengers,
      handleQueryPassenger,
      passengerOptions,
      passengerChecks,
      tickets,
      PASSENGER_TYPE_ARRAY,
      visible,
      finishCheckPassenger,
      chooseSeatType,
      chooseSeatObj,
      SEAT_COL_ARRAY,
      handleOk
    }
  },
});
</script>

<style>
.order-train .order-train-main {
  font-size: 18px;
  font-weight: bold;
}
.order-train .order-train-ticket {
  margin-top: 15px;
}
.order-train .order-train-ticket .order-train-ticket-main {
  color: red;
  font-size: 18px;
}

.order-tickets {
  margin: 10px 0;
}
.order-tickets .ant-col {
  padding: 5px 10px;
}
.order-tickets .order-tickets-header {
  background-color: cornflowerblue;
  border: solid 1px cornflowerblue;
  color: white;
  font-size: 16px;
  padding: 5px 0;
}
.order-tickets .order-tickets-row {
  border: solid 1px cornflowerblue;
  border-top: none;
  vertical-align: middle;
  line-height: 30px;
}

.order-tickets .choose-seat-item {
  margin: 5px 5px;
}
</style>