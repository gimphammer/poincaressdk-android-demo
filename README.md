## PoincaresSDK简介

PoincaresSDK是一款有poincares.com发布的网络诊断SDK，它具备ICMP Ping、HTTP、MTR以及TCP Ping多种诊断方式。PoincaresSDK配合服务器端的大数据分析与挖掘，向用户提供详尽全面的网络状况分析与诊断，可为用户网络选型提供有力的决策依据。



## 前置准备

在使用SDK前，需先在[官网](www.poincares.com)申请AppKey以及AppSecret。并替换demo PoincaresSessionWrapper中的成员变量：

```kotlin
    private var appKey : String    = "to apply on www.poincares.com"
    private var appSecret : String = "to apply on www.poincares.com"
```



## Android SDK API使用说明

SDK核心类是PoincaresSession，用户可通过PoincaresSession来进行网络侦测的各项操作。

### PoincaresSession创建

```java
PoincaresSession* pcsSession = PoincaresFactory.createSesion();
```

通过`PoincaresFactory`先创建一个Session



### PoincaresSession初始化

Session初始化接口如下：

```java
    public abstract int init(String appKey, String appSecret, NDAppTag appTag,
                             String schedulingServerUrl,
                             NDOperationObserver opObserver,
                             Context ctx);
```

appKey ：是用于集成本SDK的Application标识，可在poincares.com官网申请。

appSecret：会在官网申请appKey是一并分配。

appTag：所涉及到的java数据结构类NDAppTag主要是对app的一些附属描述，详情可以参阅SDK JavaDoc

schedulingServerUrl：是调度服务器地址，SDK在拉取app用户在服务器侧的探测任务配置时，需要用到

opObserver：是操作回调。PoincaresSDK 除了stop，其他接口都是异步接口。因此针对PoincaresSession的某项调用（比如start()调用）最终的调用结果，会通过该observer反馈给应用层。在实现该interface时，请特别留意，请勿在回调里运行重负载操作。建议，可以将回调的信息抛到其他线程做进一步处理。详情，可以参阅Demo。有关作用在Session上的各种Operation的具体类型可参与SDK Java Doc中PoincaresSession.NDOperationType一节。

Ctx: 即android应用句柄



### PoincaresSession启动

当`init`调用成功后，即可进一步调用以下接口：

```java
int res = session.start()
```

通过Session的start()接口，启动网络侦测。sdk会连接之前的schedulingServerUrl地址，拉取任务配置，并启动相应任务。



### PoincaresSession停止

相应的停止接口为:

```java
public abstract int stop();
```



### PoincaresSession去初始化

```java
public abstract void uninit();
```

Session停止后，如不再使用，需进行uninit()，以释放底层资源。



## Android SDK 权限说明

### 基本权限

PoincaresSDK的集成，需要用到以下android权限

```xml
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```



### 非加密HTTP权限(不推荐)

如果要对非加密HTTP目标进行探测，还需要在AndroidManifest.xml添加`android:networkSecurityConfig`配置，如下：

```xml
<application
			android:networkSecurityConfig="@xml/network_security_config" >
```



`network_security_config`参考配置如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">http://www.example.com</domain>
    </domain-config>
</network-security-config>
```

但处于安全的考虑，我们并不建议这样做。对于HTTP探测，我们建议取HTTPS目标。





