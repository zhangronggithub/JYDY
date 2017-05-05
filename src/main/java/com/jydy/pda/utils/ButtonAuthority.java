package com.jydy.pda.utils;

/**
 * Created by 23923 on 2017/1/10.
 */

/// <summary>
///每个功能点按钮对应的数值. 定义为2^n次方(n>0)
///每个窗体最多支持20个功能按钮(权限按钮)，对于一般的系统来讲完全足够。
/// </summary>
public class ButtonAuthority
{
    /// <summary>
    /// 未定义
    /// </summary>
    public  int NONE = 0;

    /// <summary>
    /// 查询
    /// </summary>
    public static int QueryData = 1;

    /// <summary>
    /// 新增
    /// </summary>
    public  int ADD = 2;

    /// <summary>
    /// 删除
    /// </summary>
    public static int DELETE = 4;

    /// <summary>
    /// 修改
    /// </summary>
    public  int EDIT = 8;

    /// <summary>
    /// 导入Excel
    /// </summary>
    public  int IMPORT_EXCEL = 16;

    /// <summary>
    /// 导出Excel
    /// </summary>
    public  int EXPORT_EXCEL = 32;

    /// <summary>
    /// 打印
    /// </summary>
    public  int PRINT = 64;

    /// <summary>
    /// 作废
    /// </summary>
    public  int CANCEL = 128;

    /// <summary>
    /// 恢复待检
    /// </summary>
    public  int RECOVERY = 256;

    /// <summary>
    /// 保存
    /// </summary>
    public static int SAVE = 512;

    /// <summary>
    /// 提交
    /// </summary>
    public static int SUBMIT = 1024;

    /// <summary>
    /// 扩展权限
    /// </summary>
    public  int BuPrint = 2048;

    /// <summary>
    /// 扩展权限
    /// </summary>
    public  int UNDO = 4096;

    /// <summary>
    /// 扩展权限
    /// </summary>
    public int RESERVED = 8192;

    /// <summary>
    /// 扩展权限
    /// </summary>
    public  int SHOW_VER_HISTORY = 16384;

    /// <summary>
    /// 扩展权限
    /// </summary>
    public  int SHOW_MOD_HISTORY = 32768;

    /// <summary>
    /// 扩展权限EX_01:65566
    /// </summary>
    public int EX_01 = 65536;

    /// <summary>
    /// 扩展权限EX_02:131072
    /// </summary>
    public  int EX_02 = 131072;

    /// <summary>
    /// 扩展权限EX_03:262144
    /// </summary>
    public int EX_03 = 262144;

    /// <summary>
    /// 扩展权限EX_04:524288
    /// </summary>
    public int EX_04 = 524288;


}

