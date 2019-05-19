AWS DynamoDB Notes
==================

NameMap and ValueMap
-------------------
 
### Basic

* 如果有用到表達式 (Expression) 就需要
* `ValueMap` 相當於 `Map<String, Object>`，`NameMap 相當於 Map<String, String>`，可互通

例如:

```java
Map<String, String> nameMap = new HashMap<>();
nameMap.put("#yr", "year");
Map<String, Object> valueMap = new HashMap<>();
valueMap.put(":yr", year);
```

完全相等於:

```java
NameMap nameMap = new NameMap().with("#yr", "year");
ValueMap valueMap = new ValueMap().withNumber(":yr", year);
```

### Placeholders

* value: `:yr`, name: `#yr`
* `:` 是用於 value(`ValueMap`) 的 placeholder，`#` 是 attribute name(`NameMap`) 的 placeholder，完全無法混用
* 比如用正確的佔位符，但都定義在 valueMap 也不行，會 Syntax error


### ValueMap 基本

* 可提供 value 的替換
* 如果有用到表達式 (Expression) 則必用
* 使用原因: 無法在任何表達式 (包括 KeyConditionExpression) 中使用常數

### NameMap 基本

* 可提供 attribute name 的替換
* 非必用，也可以在 expression 裡直接實字表示
* 使用原因: DynamoDB 有保留字，有些 attribute 的名字如果剛好衝到，就無法在 expression 裡直接使用(包括 KeyConditionExpression)
    * E.g., year, roles


### NameMap 不可直接替換到子物件

Not work:
```java
ScanSpec scanSpec = new ScanSpec()
        .withFilterExpression("#info_lang = :lang")
        .withNameMap(new NameMap().with("#info_lang", "detail_info.language"))
        .withValueMap(new ValueMap().withString(":lang", lang));
```

Works:
```java
ScanSpec scanSpec = new ScanSpec()
        .withFilterExpression("detail_info.#lang = :lang")
        .withNameMap(new NameMap().with("#lang", "language"))
        .withValueMap(new ValueMap().withString(":lang", lang));
```
