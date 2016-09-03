#!/bin/sh

#===========================================================================================
# Java 环境设置
#===========================================================================================
error_exit ()
{
    echo "ERROR: $1 !!"
    exit 1
}

[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/opt/taobao/java
[ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=$(dirname $0)/..
export CLASSPATH=.:${BASE_DIR}/conf:${CLASSPATH}

#===========================================================================================
# JVM 参数配置
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -server -Xms4g -Xmx4g -Xmn2g -XX:PermSize=128m -XX:MaxPermSize=320m"
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 -XX:+DisableExplicitGC"
JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:${HOME}/rmq_bk_gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -XX:-HeapDumpOnOutOfMemoryError"
JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${BASE_DIR}/lib"
JAVA_OPT="${JAVA_OPT} -Dcom.rocketmq.remoting.socket.sndbuf.size=8388608 -Dcom.rocketmq.remoting.socket.rcvbuf.size=8388608"
JAVA_OPT="${JAVA_OPT} -Drocketmq.namesrv.domain=config.graphene.spellso.com"
JAVA_OPT="${JAVA_OPT} -Duser.timezone=UTC"
JAVA_OPT="${JAVA_OPT} -DRocketMQServerPassword=VVYZZ9NLVdy849XIy/tM3Q=="
JAVA_OPT="${JAVA_OPT} -DRocketMQClientPassword=VVYZZ9NLVdy849XIy/tM3Q=="
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"

numactl --interleave=all pwd > /dev/null 2>&1
if [ $? -eq 0 ]
then
    numactl --interleave=all $JAVA ${JAVA_OPT} $@
else
    $JAVA ${JAVA_OPT} $@
fi
