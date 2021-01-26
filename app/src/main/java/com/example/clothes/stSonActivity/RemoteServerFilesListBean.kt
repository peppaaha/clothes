package com.example.clothes.stSonActivity

class RemoteServerFilesListBean : ArrayList<RemoteServerFilesListBeanItem>()

data class RemoteServerFilesListBeanItem(
    val mtime: String,
    val name: String,
    val type: String
)