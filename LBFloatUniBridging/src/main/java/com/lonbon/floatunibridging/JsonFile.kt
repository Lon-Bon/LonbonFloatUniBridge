package com.lonbon.floatunibridging

/**
 * *****************************************************************************
 * <p>
 * Copyright (C),2007-2016, LonBon Technologies Co. Ltd. All Rights Reserved.
 * <p>
 * *****************************************************************************
 *
 * @ProjectName:  LBFloatUniDemo
 * @Package:      com.lonbon.floatunibridging
 * @ClassName: JsonFile
 * @Author：    neo
 * @Create:    2023/2/22
 * @Describe:
 */
data class JsonFile(val path:String,val fileName:String,val fileSize:Long,val directory:Boolean,val hasChildFileList:Boolean)
