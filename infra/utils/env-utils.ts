/**
 * Checks whether the given environment is production.
 */
export function isProd(envName: String): boolean {
  return envName === "prod";
}