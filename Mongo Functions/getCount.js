// Sample Command : getCount("Metadata", "brand", [/.*D.*/])

function getCount(collectionName, columnName, values) {

	var col = db[collectionName];
	
	var query= {};
	query[columnName] = {$in: values};
	
	return col.aggregate(
		[
			{$match:  query } 
			,{$group: { _id : '$'+columnName ,total: {$sum:1}}}
		]
	)
}



