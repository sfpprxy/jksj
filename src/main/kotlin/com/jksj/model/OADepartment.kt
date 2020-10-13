package com.jksj.model

data class OADepartment (
	var id : String,
	var deleteFlag : Int,
	var createdId : String,
	var createdBy : String,
	var createdAt : String,
	var modifiedId : String,
	var modifiedBy : String,
	var modifiedAt : String,
	var dataArea : String,
	var parentId : String,
	var state : String,
	var isLeaf : Int,
	var children : String,
	var hierarchicalCode : String,
	var deptName : String,
	var deptCode : String,
	var isCompany : Int,
	var companyName : String,
	var isDivisional : Int,
	var syncTime : String,
	var jkyFlag : Int
)
