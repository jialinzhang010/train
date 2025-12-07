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