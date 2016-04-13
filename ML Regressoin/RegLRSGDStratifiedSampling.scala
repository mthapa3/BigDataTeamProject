import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.feature.PCA

object RegLRSGDStratifiedSampling{

def main(args: Array[String]) {
	// Create new Spark Context
	val conf = new SparkConf().setAppName("RegLRSGDStratifiedSampling").setMaster("local[2]")
	val sc = new SparkContext(conf)

val Delimeter = "\t"
val data = sc.textFile("C://Users//Bala///Downloads//sample200.txt")
val parsedData = data.map { line =>
val parts = line.split("""\""" + Delimeter)
LabeledPoint(parts(1).split("""\|""")(1).toDouble, Vectors.dense(parts.slice(2,86).map(x => x.split("""\|""")(1).toDouble).toArray))
}

//PCA
val pca = new PCA(50).fit(parsedData.map(_.features))
val projected = parsedData.map(p => p.copy(features = pca.transform(p.features)))

val splits = projected.randomSplit(Array(0.6, 0.4), seed = 11L)
val training = splits(0).cache()
val test = splits(1).cache()

// LR default L2
val lrAlg = new LinearRegressionWithSGD()
lrAlg.optimizer
.setStepSize(0.01)	
lrAlg.setIntercept(true)
val model = lrAlg.run(training)

// Training Data Prediction & Score

val labelAndPreds = training.map { point =>
val prediction = model.predict(point.features)
(point.label, prediction)
}


val trainingMetrics = new RegressionMetrics(labelAndPreds)
// Get evaluation metrics.
println("\nRoot Mean Squared Error = " + trainingMetrics.rootMeanSquaredError)


// Test Data Prediction & Score


val testlabelAndPreds = test.map { case LabeledPoint(label, features) =>
val prediction = model.predict(features)
(prediction, label)
}

// Get evaluation metrics.
val testMetrics = new RegressionMetrics(testlabelAndPreds)

	println("########################")
	println(testlabelAndPreds.foreach(println))
	println("########################")

println("Root Mean Squared Error : " + testMetrics.rootMeanSquaredError)
	}
}