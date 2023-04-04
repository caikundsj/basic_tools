# basic_tools

Apache Flink是一个开源计算平台，旨在支持面向分布式数据流处理和批量数据处理两种类型的应用。该平台提供了基本API，如DataSet、DataStream、Table、SQL等，并包含了常用特性，例如Time & Window、窗口函数、Watermark、触发器、分布式缓存、异步IO、侧输出和广播等。此外，本文还将涵盖Flink高级应用，如ProcessFunction和状态管理等知识点。

出于构建 `Flink` 通用的基础环境，按模块划分相应功能，统一任务入口，规范作业为目的制作此规范。

## base-env

构建flink的基础环境，定义流批入口。

统一管理作业入口。
