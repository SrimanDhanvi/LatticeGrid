package com.example.latticegrid

class MatrixBenchmark {

    data class BenchmarkResult(
        val matrixSize: Int,
        val executionTimeMs: Long
    )

    fun runBenchmark(size: Int): BenchmarkResult {

        val A = MatrixTask.generateMatrix(size)
        val B = MatrixTask.generateMatrix(size)

        val startTime = System.currentTimeMillis()

        MatrixTask.multiply(A, B)

        val endTime = System.currentTimeMillis()

        return BenchmarkResult(
            matrixSize = size,
            executionTimeMs = endTime - startTime
        )
    }
}