package com.example.daydayup.respository.model


/**
 * curPage : 1
 * datas : [{"apkLink":"","author":"红橙Darren","chapterId":245,"chapterName":"集合相关","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":3326,"link":"https://www.jianshu.com/p/9edd74769f21","niceDate":"2018-08-27","origin":"","projectLink":"","publishTime":1535372956000,"superChapterId":245,"superChapterName":"Java深入","tags":[],"title":"数据结构算法 - 栈和队列","type":0,"userId":-1,"visible":1,"zan":0}]
 * offset : 0
 * over : false
 * pageCount : 80
 * size : 20
 * total : 1596
 */
data class ArticleResponseBody<T>(var curPage: Int = 0,
                                  var offset: Int = 0,
                                  var isOver: Boolean = false,
                                  var pageCount: Int = 0,
                                  var size: Int = 0,
                                  var total: Int = 0,
                                  var datas: List<T>? = null)


/**
 * apkLink :
 * author : 红橙Darren
 * chapterId : 245
 * chapterName : 集合相关
 * collect : false
 * courseId : 13
 * desc :
 * envelopePic :
 * fresh : false
 * id : 3326
 * link : https://www.jianshu.com/p/9edd74769f21
 * niceDate : 2018-08-27
 * origin :
 * projectLink :
 * publishTime : 1535372956000
 * superChapterId : 245
 * superChapterName : Java深入
 * tags : []
 * title : 数据结构算法 - 栈和队列
 * type : 0
 * userId : -1
 * visible : 1
 * zan : 0
 */
data class ArticleBean(var apkLink: String? = null,
                       var author: String? = null,
                       var chapterId: Int = 0,
                       var chapterName: String? = null,
                       var collect: Boolean = false,
                       var courseId: Int = 0,
                       var desc: String? = null,
                       var envelopePic: String? = null,
                       var fresh: Boolean = false,
                       var id: Int = 0,
                       var link: String? = null,
                       var niceDate: String? = null,
                       var origin: String? = null,
                       var originId: Int? = null,
                       var projectLink: String? = null,
                       var publishTime: Long = 0,
                       var superChapterId: Int = 0,
                       var superChapterName: String? = null,
                       var title: String? = null,
                       var type: Int = 0,
                       var userId: Int = 0,
                       var visible: Int = 0,
                       var zan: Int = 0,
                       var tags: List<Project>? = null)


data class Project(var name:String?,var url:String?)