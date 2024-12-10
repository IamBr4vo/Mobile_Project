package com.example.innovalink

data class Project(
    val id: Int,
    val name: String,
    val subtitle: String,
    val content: String,
    val imagePath: String?,
    val author: String,
    val gmail: String,
    var likes: Int = 0, // Contador de likes
    val likedBy: MutableSet<Int> = mutableSetOf(), // IDs de los usuarios que dieron like
    var userHasLiked: Boolean = false

) {
    // Método para manejar likes (dar o quitar)
    fun toggleLike(userId: Int): Boolean {
        return if (likedBy.contains(userId)) {
            // Si ya dio like, quitarlo
            likedBy.remove(userId)
            likes--
            false // Like quitado
        } else {
            // Si no ha dado like, agregarlo
            likedBy.add(userId)
            likes++
            true // Like añadido
        }
    }
}
