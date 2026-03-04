UPDATE `inventoryitems` 
join `storages` on `inventoryitems`.`accountid`  = `storages`.`storageid` 
set `inventoryitems`.`accountid` = `storages`.`accountid`;

ALTER TABLE `storages` ADD COLUMN `type` INT(11) NOT NULL DEFAULT '0';
ALTER TABLE `storages` CHANGE `accountid` `ownerId` INT(11) NOT NULL DEFAULT '0';
