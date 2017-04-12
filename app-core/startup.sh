#!/bin/sh
set -e
PROGRAME_HOME=/ky/otter/cannalDemo/app-core/target
LAUNCH_CLASS=com.alibaba.otter.canal.sample.SimpleCanalClientExample
RESOURCE_PATH=classes/
GC_LOG=${PROGRAME_HOME}/logs/gc.log
#JAVA_HOME=/usr/local/jdk1.7.0_79/bin
cd ${PROGRAME_HOME}

cpath=$CLASSPATH:${RESOURCE_PATH}
case "$1" in
  start)
    for file in lib/*.jar; do
      cpath=$cpath:$file
    done
    nohup java -cp $cpath -Xmx2000m -Xms2000m -Xmn500m -Xss256k  -XX:PermSize=128m -Xloggc:${GC_LOG} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0  -Dname=cardsSchedule -server -Dsun.net.client.defaultConnectTimeout=120000 -Dsun.net.client.defaultReadTimeout=240000 -Djava.awt.headless=true ${LAUNCH_CLASS} > console.log 2>&1 &
    echo $! > process.pid
  ;;

  stop)
    pid=`cat process.pid`
    if [ -n "$pid" ]; then
      kill $pid
    fi
  ;;

  restart)
    echo "restart..."
    sh $0 stop
    sleep 1
    sh $0 start
  ;;

  *)
  echo "Usage: run.sh {start|stop|restart}"
  ;;
esac
exit 0
