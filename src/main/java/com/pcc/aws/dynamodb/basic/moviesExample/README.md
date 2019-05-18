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

### ItemScan

#### `scanAllItems(table)`:
```
2019/05/18 18:39:54 INFO  [ItemScan.java:40] : Scan item 1: { Item: {year=1997, detail_info={release_date=1997-11-01T00:00:00Z, directors=[James Francis Cameron], roles=[Jack Dawson, Rose DeWitt Bukater], rating=10, language=English}, title=Titanic} }
2019/05/18 18:39:54 INFO  [ItemScan.java:40] : Scan item 2: { Item: {year=2019, detail_info={roles=[Iron Man, Captain America, Hulk, Black Widow], rating=8.32}, title=Avengers: Endgame} }
2019/05/18 18:39:54 INFO  [ItemScan.java:40] : Scan item 3: { Item: {year=2009, detail_info={release_date=2009-09-30T00:00:00Z, directors=[Chen Kuo-Fu, Kao Chun-Shu], roles=[Ku Hsiao-Meng, Wu Chih-Kuo, Takeda, Li Ning-Yu], rating=9.5, language=Mandarin}, title=The Message} }
2019/05/18 18:39:54 INFO  [ItemScan.java:40] : Scan item 4: { Item: {year=2001, detail_info={release_date=2001-12-21T00:00:00Z, directors=[Ron Howard], roles=[John Nash, Alicia Nash], rating=6.5, language=English}, title=A Beautiful Mind} }
2019/05/18 18:39:54 INFO  [ItemScan.java:40] : Scan item 5: { Item: {year=2001, detail_info={release_date=2001-07-20T00:00:00Z, directors=[Hayao Miyazaki], roles=[Sen, Haku, No-Face], rating=7, language=Japanese}, title=Spirited Away} }
2019/05/18 18:39:54 INFO  [ItemScan.java:42] : Scan 5 items succeeded.
```

#### `scanItemsByYearRange(table, 1990, 2012)`:
```
2019/05/18 18:39:54 INFO  [ItemScan.java:72] : Scan item 1: { Item: {year=1997, title=Titanic} }
2019/05/18 18:39:54 INFO  [ItemScan.java:72] : Scan item 2: { Item: {year=2009, title=The Message} }
2019/05/18 18:39:54 INFO  [ItemScan.java:72] : Scan item 3: { Item: {year=2001, title=A Beautiful Mind} }
2019/05/18 18:39:54 INFO  [ItemScan.java:72] : Scan item 4: { Item: {year=2001, title=Spirited Away} }
2019/05/18 18:39:54 INFO  [ItemScan.java:74] : Scan 4 items with year 1990 ~ 2012 succeeded.
```

#### `scanItemsByTitleStartingWith(table, "A", "S")`:
```
2019/05/18 18:39:54 INFO  [ItemScan.java:102] : Scan item 1: {
  "year" : 2019,
  "detail_info" : {
    "roles" : [ "Iron Man", "Captain America", "Hulk", "Black Widow" ],
    "rating" : 8.32
  },
  "title" : "Avengers: Endgame"
}
2019/05/18 18:39:54 INFO  [ItemScan.java:102] : Scan item 2: {
  "year" : 2001,
  "detail_info" : {
    "release_date" : "2001-12-21T00:00:00Z",
    "directors" : [ "Ron Howard" ],
    "roles" : [ "John Nash", "Alicia Nash" ],
    "rating" : 6.5,
    "language" : "English"
  },
  "title" : "A Beautiful Mind"
}
2019/05/18 18:39:54 INFO  [ItemScan.java:104] : Scan 2 items with title A-S succeeded
```

#### `scanItemsByLanguage(table, "English")`:
```
2019/05/18 19:13:04 INFO  [ItemScan.java:132] : Scan item 1: {
  "year" : 1997,
  "detail_info" : {
    "release_date" : "1997-11-01T00:00:00Z",
    "directors" : [ "James Francis Cameron" ],
    "roles" : [ "Jack Dawson", "Rose DeWitt Bukater" ],
    "rating" : 10,
    "language" : "English"
  },
  "title" : "Titanic"
}
2019/05/18 19:13:04 INFO  [ItemScan.java:132] : Scan item 2: {
  "year" : 2001,
  "detail_info" : {
    "release_date" : "2001-12-21T00:00:00Z",
    "directors" : [ "Ron Howard" ],
    "roles" : [ "John Nash", "Alicia Nash" ],
    "rating" : 6.5,
    "language" : "English"
  },
  "title" : "A Beautiful Mind"
}
2019/05/18 19:13:04 INFO  [ItemScan.java:134] : Scan 2 items with English language succeeded.
```

#### `scanItemsByRatingThreshold(table, 7.99f)`:
```
2019/05/18 19:13:05 INFO  [ItemScan.java:158] : Scan item 1: {
  "year" : 1997,
  "detail_info" : {
    "release_date" : "1997-11-01T00:00:00Z",
    "directors" : [ "James Francis Cameron" ],
    "roles" : [ "Jack Dawson", "Rose DeWitt Bukater" ],
    "rating" : 10,
    "language" : "English"
  },
  "title" : "Titanic"
}
2019/05/18 19:13:05 INFO  [ItemScan.java:160] : Scan 1 items with rating >= 9.600000 succeeded.
```

### ItemRead

```
2019/05/18 21:16:08 INFO  [ItemRead.java:25] : GetItem succeeded: {
  "year" : 2019,
  "detail_info" : {
    "roles" : [ "Iron Man", "Captain America", "Hulk", "Black Widow" ],
    "rating" : 8.32
  },
  "title" : "Avengers: Endgame"
}
```