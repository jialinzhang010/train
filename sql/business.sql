drop table if exists `member`;
create table `member` (
    `id` bigint not null comment 'id',
    `mobile` varchar(11) comment 'phone number',
    primary key (`id`),
    unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='member';

drop table if exists `station`;
create table `station` (
    `id` bigint not null comment 'id',
    `name` varchar(20) not null comment 'Station name',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`),
    unique key `name_unique` (`name`)
) engine=innodb default charset=utf8mb4 comment='Station';

drop table if exists `train`;
create table `train` (
    `id` bigint not null comment 'id',
    `code` varchar(20) not null comment 'Train code',
    `type` char(1) not null comment 'Train Type | Enum[TrainTypeEnum]',
    `start` varchar(20) not null comment 'Departure station',
    `start_time` time not null comment 'Departure time',
    `end` varchar(20) not null comment 'Terminal station',
    `end_time` time not null comment 'Arrival time',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`),
    unique key `code_unique` (`code`)
) engine=innodb default charset=utf8mb4 comment='Train code';

drop table if exists `train_station`;
create table `train_station` (
    `id` bigint not null comment 'id',
    `train_code` varchar(20) not null comment 'Train code',
    `index` int not null comment 'Station order',
    `name` varchar(20) not null comment 'Station name',
    `in_time` time comment 'Arrival time',
    `out_time` time comment 'Departure time',
    `stop_time` time comment 'Dwell time',
    `km` decimal(8, 2) not null comment 'Distance',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`),
    unique key `train_code_index_unique` (`train_code`, `index`),
    unique key `train_code_name_unique` (`train_code`, `name`)
) engine=innodb default charset=utf8mb4 comment='Train station';

drop table if exists `train_carriage`;
create table `train_carriage` (
    `id` bigint not null comment 'id',
    `train_code` varchar(20) not null comment 'Train code',
    `index` int not null comment 'Carriage number',
    `seat_type` char(1) not null comment 'Seat type | Enum[SeatTypeEnum]',
    `seat_count` int not null comment 'Seat count',
    `row_count` int not null comment 'Row count',
    `col_count` int not null comment 'Column count',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    unique key `train_code_index_unique` (`train_code`, `index`),
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Train carriage';

drop table if exists `train_seat`;
create table `train_seat` (
    `id` bigint not null comment 'id',
    `train_code` varchar(20) not null comment 'Train code',
    `carriage_index` int not null comment 'Carriage index',
    `row` char(2) not null comment 'Row number | 01, 02',
    `col` char(1) not null comment 'Column number | Enum[SeatColEnum]',
    `seat_type` char(1) not null comment 'Seat type | Enum[SeatTypeEnum]',
    `carriage_seat_index` int not null comment 'Carriage seat index',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Seat';

drop table if exists `daily_train`;
create table `daily_train` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'Date',
    `code` varchar(20) not null comment 'Train code',
    `type` char(1) not null comment 'Train Type | Enum[TrainTypeEnum]',
    `start` varchar(20) not null comment 'Departure station',
    `start_time` time not null comment 'Departure time',
    `end` varchar(20) not null comment 'Terminal station',
    `end_time` time not null comment 'Arrival time',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`),
    unique key `code_unique` (`date`,`code`)
) engine=innodb default charset=utf8mb4 comment='Daily train code';

drop table if exists `daily_train_station`;
create table `daily_train_station` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'date',
    `train_code` varchar(20) not null comment 'Train code',
    `index` int not null comment 'Station order',
    `name` varchar(20) not null comment 'Station name',
    `in_time` time comment 'Arrival time',
    `out_time` time comment 'Departure time',
    `stop_time` time comment 'Dwell time',
    `km` decimal(8, 2) not null comment 'Distance',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`),
    unique key `train_code_index_unique` (`date`, `train_code`, `index`),
    unique key `train_code_name_unique` (`date`, `train_code`, `name`)
) engine=innodb default charset=utf8mb4 comment='Daily train station';

drop table if exists `daily_train_carriage`;
create table `daily_train_carriage` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'date',
    `train_code` varchar(20) not null comment 'Train code',
    `index` int not null comment 'Carriage number',
    `seat_type` char(1) not null comment 'Seat type | Enum[SeatTypeEnum]',
    `seat_count` int not null comment 'Seat count',
    `row_count` int not null comment 'Row count',
    `col_count` int not null comment 'Column count',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    unique key `date_train_code_index_unique` (`date`, `train_code`, `index`),
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Daily train carriage';

drop table if exists `daily_train_seat`;
create table `daily_train_seat` (
    `id` bigint not null comment 'id',
    `date` date not null comment 'date',
    `train_code` varchar(20) not null comment 'Train code',
    `carriage_index` int not null comment 'Carriage index',
    `row` char(2) not null comment 'Row number | 01, 02',
    `col` char(1) not null comment 'Column number | Enum[SeatColEnum]',
    `seat_type` char(1) not null comment 'Seat type | Enum[SeatTypeEnum]',
    `carriage_seat_index` int not null comment 'Carriage seat index',
    `sell` varchar(50) not null comment 'Sales status | Generate a segment availability string using 0s and 1s, where 0 indicates "available for sale" and 1 indicates "not available for sale."',
    `create_time` datetime(3) comment 'Create time',
    `update_time` datetime(3) comment 'Update time',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='Daily seat';