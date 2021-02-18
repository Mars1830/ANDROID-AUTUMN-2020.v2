package com.example.game

interface Mover {
    public enum class Command {concede, propose_draw, accept_draw, refuse_draw}
    fun DoMove(currentPosition: String, nextPosition: String)
    fun DoRemove(position: String)
    fun DoCommand(cmd: Command)
}