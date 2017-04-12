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

关于订阅
```
mysql 数据解析关注的表，Perl正则表达式.  
多个正则之间以逗号(,)分隔，转义符需要双斜杠(\\)  
  
常见例子：  
1.  所有表：.*   or  .*\\..*  
2.  canal schema下所有表： canal\\..*  
3.  canal下的以canal打头的表：canal\\.canal.*  
4.  canal schema下的一张表：canal.test1  
5.  多个规则组合使用：canal\\..*,mysql.test1,mysql.test2 (逗号分隔)  
```
