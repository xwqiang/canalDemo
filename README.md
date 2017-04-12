cannel 条件：

配置binlog
```
[mysqld]
log-bin=mysql-bin #添加这一行就ok
binlog-format=ROW #选择row模式
server_id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复
```
canal的原理是模拟自己为mysql slave，所以这里一定需要做为mysql slave的相关权限.
```
CREATE USER canal IDENTIFIED BY 'canal';  
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;
FLUSH PRIVILEGES;
```
查看权限
```
show grants for 'root'@'localhost'
```
启动cannel服务

[地址](https://github.com/alibaba/canal/wiki/QuickStar)

常用命令：
```
SHOW MASTER STATUS
SHOW SLAVE STATUS
SHOW BINARY LOGS
show binlog events
```