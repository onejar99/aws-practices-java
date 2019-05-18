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
