drop table if exists `member`;
create table `member` (
    `id` bigint not null comment 'id',
    `mobile` varchar(11) comment 'phone number',
    primary key (`id`),
    unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='member';

drop table if exists `passenger`;
create table `passenger` (
    `id` bigint not null comment 'id',
    `member_id` bigint not null comment 'member id',
    `name` varchar(20) not null comment 'name',
    `id_card` varchar(18) not null comment 'ID card',
    `type` char(1) not null comment 'passenger type|enum[PassengerTypeEnum]',
    `create_time` datetime(3) comment 'create time',
    `update_time` datetime(3) comment 'update time',
    primary key (`id`),
    index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='passenger';

drop table if exists `ticket`;
create table `ticket` (
  `id` bigint not null comment 'ID',
  `member_id` bigint not null comment 'Member ID',
  `passenger_id` bigint not null comment 'Passenger ID',
  `passenger_name` varchar(20) comment 'Passenger Name',
  `train_date` date not null comment 'Travel Date',
  `train_code` varchar(20) not null comment 'Train Number',
  `carriage_index` int not null comment 'Carriage Number',
  `seat_row` char(2) not null comment 'Row Number | 01, 02',
  `seat_col` char(1) not null comment 'Column | Enum [SeatColEnum]',
  `start_station` varchar(20) not null comment 'Departure Station',
  `start_time` time not null comment 'Departure Time',
  `end_station` varchar(20) not null comment 'Arrival Station',
  `end_time` time not null comment 'Arrival Time',
  `seat_type` char(1) not null comment 'Seat Type | Enum [SeatTypeEnum]',
  `create_time` datetime(3) comment 'Creation Time',
  `update_time` datetime(3) comment 'Last Update Time',
  primary key (`id`),
  index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='ticket';

