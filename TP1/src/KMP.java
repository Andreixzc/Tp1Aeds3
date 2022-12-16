class KMP {
	void KMPSearch(String padrao, String texto) {
		int M = padrao.length();
		int N = texto.length();

		// lps[] vai guardar o maior sufixo encontrado
		int lps[] = new int[M];
		int j = 0; // index for padrao[]

		armazenaSufixo(padrao, M, lps);

		int i = 0; // index for texto[]
		while ((N - i) >= (M - j)) {
			if (padrao.charAt(j) == texto.charAt(i)) {
				j++;
				i++;
			}
			if (j == M) {
				System.out.println("Padrao encontrado " + "no indice " + (i - j));
				j = lps[j - 1];
			}

			// mismatch after j matches
			else if (i < N && padrao.charAt(j) != texto.charAt(i)) {
				if (j != 0)
					j = lps[j - 1];
				else
					i = i + 1;
			}
		}
	}

	void armazenaSufixo(String padrao, int M, int lps[]) {
		// lenght do maior sufixo
		int len = 0;
		int i = 1;
		lps[0] = 0; // lPS Sempre começa com 0
		while (i < M) {
			if (padrao.charAt(i) == padrao.charAt(len)) {
				len++;
				lps[i] = len;
				i++;
			} else // (padrao[i] != padrao[len])
			{

				if (len != 0) {
					len = lps[len - 1];


				} else // se o lenght for 0
				{
					lps[i] = len;
					i++;
				}
			}
		}
	}

}
