#!/bin/bash

usage() { echo "Usage: $0 [-t] [-n testNo]" 1>&2; exit 1; }

runner=Main
testNo=1

while getopts "tn:" option; do
    case $option in
        t)
            runner=Test
            ;;
        n)
            testNo=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done

javac $runner.java src/Matrix.java src/MatrixChain.java src/ParallelOptimizationChain.java
java $runner $testNo
rm $runner.class src/Matrix.class src/MatrixChain.class src/ParallelOptimizationChain.class src/ParallelOptimizationChain\$OrderingWorker.class