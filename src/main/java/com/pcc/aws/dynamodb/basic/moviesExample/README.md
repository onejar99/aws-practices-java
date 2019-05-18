AWS DynamoDB Basic Example: Movies
==========================================

Prerequisites
---------------------
Local AWS configuration and credential files are configured.
* `~/.aws/config`
* `~/.aws/credentials`


Table Schema
---------------------

| year (Partition Key) | title (Sort Key) | detailInfo |
|---|---|---|
|||


Running
---------------------

### TableCreation

```
2019/05/18 15:51:30 INFO  [TableCreation.java:32] : Creating Table, it may take few seconds... (Table status: CREATING)
2019/05/18 15:51:41 INFO  [TableCreation.java:34] : Created and activated Table successfully. (Table status: ACTIVE)
```

### TableDeletion

```
2019/05/18 16:25:36 INFO  [TableDeletion.java:21] : Deleting Table, it may take few seconds... (Table object: {Movies7: null})
2019/05/18 16:25:42 INFO  [TableDeletion.java:23] : Deleted Table successfully.
```

### ItemAddOrReplace

```
2019/05/18 18:12:23 INFO  [ItemAddOrReplace.java:98] : Put item year=[2019] title=[Avengers: Endgame] succeeded: {}
2019/05/18 18:12:23 INFO  [ItemAddOrReplace.java:98] : Put item year=[2009] title=[The Message] succeeded: {}
2019/05/18 18:12:24 INFO  [ItemAddOrReplace.java:98] : Put item year=[2001] title=[A Beautiful Mind] succeeded: {}
2019/05/18 18:12:24 INFO  [ItemAddOrReplace.java:98] : Put item year=[2001] title=[Spirited Away] succeeded: {}
2019/05/18 18:12:24 INFO  [ItemAddOrReplace.java:98] : Put item year=[1997] title=[Titanic] succeeded: {}
2019/05/18 18:12:24 INFO  [ItemAddOrReplace.java:67] : Total 4 items imported.
```
