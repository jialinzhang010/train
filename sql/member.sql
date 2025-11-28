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