package com.example.latticegrid

class MatrixTask {
    companion object {

        fun generateMatrix(size: Int): Array<DoubleArray> {
            val matrix = Array(size) { DoubleArray(size) }

            for (i in 0 until size) {
                for (j in 0 until size) {
                    matrix[i][j] = Math.random()
                }
            }

            return matrix
        }

        fun multiply(A: Array<DoubleArray>, B: Array<DoubleArray>): Array<DoubleArray> {
            val n = A.size
            val result = Array(n) { DoubleArray(n) }

            for (i in 0 until n) {
                for (j in 0 until n) {
                    for (k in 0 until n) {
                        result[i][j] += A[i][k] * B[k][j]
                    }
                }
            }

            return result
        }
    }
}