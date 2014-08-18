package org.janelia.thickness.normalization;

import ij.IJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class MaxColumnNormalization extends AbstractColumnNormalization {

	@Override
	public <T extends RealType<T>> void normalize(
			final RandomAccessibleInterval<T> input) {
		final IntervalView<T> minSlice = Views.hyperSlice( input, 2, 0 );
		final IntervalView<T> maxSlice = Views.hyperSlice( input, 2, input.dimension( 2 ) - 1 );
		
		final Cursor<T> minCursor = Views.flatIterable( minSlice ).cursor();
		final Cursor<T> maxCursor = Views.flatIterable( maxSlice ).cursor();
		
		double maxDiff  = -Double.MAX_VALUE;
		double minShift =  Double.NaN;
		
		while( minCursor.hasNext() ) {
			final double currMax = maxCursor.next().getRealDouble();
			final double currMin = minCursor.next().getRealDouble();
			final double currDiff = currMax - currMin;
			if ( currDiff > maxDiff ) {
				maxDiff  = currDiff;
				minShift = currMin; 
			}
		}
		
		if ( Double.isNaN( minShift ) )
			return;
		
		final double scalingFactor = input.dimension( 2 ) / maxDiff;
		
		IJ.log( "" + scalingFactor );
		
		this.normalize( input, scalingFactor, minShift );
	}

}
