package com.movies.models

class NodeNotFoundInLocalDatabase(
    val className: String,
    val key: String
) : Exception("$className $key") {
}