(ns utils.constants)

(def strAppName "QuickDB")

;success insert massages
(def msgCreateTableSuccess "Table creates successfully")
(def msgInsrtRecSuccess "Insert record is success")

;error masseges for exceptions
(def msgErrTableNameNotExistsDrop "Delete table failed- table name doesn't exists")
(def msgErrInvalidKey "Add record failed- The new record doesn't contain all keys")
(def msgErrInvalidfield "Add record failed- The new record contain invalid feilds")
(def msgErrInsertFailed "Add record failed")
(def msgErrTableNameExists "Create table error- table name already exists in DB")
(def msgErrKeyNotInFields "Create table error- key doesn't exist in feilds")
(def msgErrTableNameNotExists "Add record failed- table name doesn't exists")
(def msgErrTableNameNotExistsDel "Delete record failed- table name doesn't exists")
(def msgErrRecordNotExistsDel "Delete record failed- record doesn't exists")
(def msgErrInvalidCommand "Invalid Command")
(def msgErrRecordExists "Add record failed- record with same fields already exists")
